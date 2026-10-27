// Uploads every loader jar in all-jars/ to Modrinth and CurseForge, one release per jar, each tagged with its own
// Minecraft version and loader. Jar names follow <mod_id>-<loader>-<minecraft>-<version>.jar (multiloader-common.gradle).
// The changelog is the section of CHANGELOG.md for this version. Fabric jars declare Fabric API as required.
//
// env: VERSION, JAR_LIST (newline separated), MODRINTH_TOKEN, MODRINTH_ID, CURSEFORGE_TOKEN, CURSEFORGE_ID
import fs from 'node:fs';
import path from 'node:path';

const VERSION = process.env.VERSION;
const JARS = process.env.JAR_LIST.split('\n').map((s) => s.trim()).filter(Boolean);
const LOADER_NAMES = { fabric: 'Fabric', forge: 'Forge', neoforge: 'NeoForge' };
const FABRIC_API = { modrinth: 'P7dR8mSH', curseforge: 'fabric-api' };

function changelog() {
  const text = fs.readFileSync('CHANGELOG.md', 'utf8');
  const start = text.indexOf(`## ${VERSION}`);
  if (start < 0) return `Release ${VERSION}`;
  const rest = text.slice(start + 3 + VERSION.length);
  const end = rest.search(/\n## /);
  return `## ${VERSION}${end < 0 ? rest : rest.slice(0, end)}`.trim();
}

function describe(jar) {
  const m = path.basename(jar).match(/^[a-z0-9_]+-(fabric|forge|neoforge)-([0-9.]+)-(.+)\.jar$/);
  if (!m) throw new Error(`Unexpected jar name: ${jar}`);
  return { jar, loader: m[1], minecraft: m[2], file: path.basename(jar) };
}

const CHANGELOG = changelog();
const builds = JARS.map(describe);
const failures = [];

async function modrinth(b) {
  const data = {
    name: `${VERSION} (${LOADER_NAMES[b.loader]} ${b.minecraft})`,
    version_number: `${VERSION}+${b.minecraft}-${b.loader}`,
    changelog: CHANGELOG,
    dependencies: b.loader === 'fabric' ? [{ project_id: FABRIC_API.modrinth, dependency_type: 'required' }] : [],
    game_versions: [b.minecraft],
    version_type: 'release',
    loaders: [b.loader],
    featured: true,
    status: 'listed',
    project_id: process.env.MODRINTH_ID,
    file_parts: ['file'],
    primary_file: 'file',
  };
  const form = new FormData();
  form.append('data', JSON.stringify(data));
  form.append('file', new Blob([fs.readFileSync(b.jar)]), b.file);
  const res = await fetch('https://api.modrinth.com/v2/version', {
    method: 'POST', headers: { Authorization: process.env.MODRINTH_TOKEN, 'User-Agent': 'NightBeam publish workflow' }, body: form,
  });
  const text = await res.text();
  if (!res.ok) throw new Error(`Modrinth ${res.status} ${text.slice(0, 400)}`);
  console.log(`Modrinth OK ${b.file}`);
}

const CF = 'https://minecraft.curseforge.com/api';
let cfVersions;
async function cfIds(b) {
  if (!cfVersions) {
    const h = { 'X-Api-Token': process.env.CURSEFORGE_TOKEN };
    const [types, versions] = await Promise.all([
      fetch(`${CF}/game/version-types`, { headers: h }).then((r) => r.json()),
      fetch(`${CF}/game/versions`, { headers: h }).then((r) => r.json()),
    ]);
    const typeSlug = new Map(types.map((t) => [t.id, t.slug]));
    cfVersions = versions.map((v) => ({ ...v, typeSlug: typeSlug.get(v.gameVersionTypeID) || '' }));
  }
  const mc = cfVersions.find((v) => v.typeSlug.startsWith('minecraft') && v.name === b.minecraft);
  const loader = cfVersions.find((v) => v.typeSlug === 'modloader' && v.name === LOADER_NAMES[b.loader]);
  if (!mc) throw new Error(`CurseForge has no game version ${b.minecraft}`);
  if (!loader) throw new Error(`CurseForge has no loader ${LOADER_NAMES[b.loader]}`);
  return [mc.id, loader.id];
}

async function curseforge(b) {
  const metadata = {
    changelog: CHANGELOG,
    changelogType: 'markdown',
    displayName: `Core Utilities ${VERSION} (${LOADER_NAMES[b.loader]} ${b.minecraft})`,
    gameVersions: await cfIds(b),
    releaseType: 'release',
    ...(b.loader === 'fabric' ? { relations: { projects: [{ slug: FABRIC_API.curseforge, type: 'requiredDependency' }] } } : {}),
  };
  const form = new FormData();
  form.append('metadata', JSON.stringify(metadata));
  form.append('file', new Blob([fs.readFileSync(b.jar)]), b.file);
  const res = await fetch(`${CF}/projects/${process.env.CURSEFORGE_ID}/upload-file`, {
    method: 'POST', headers: { 'X-Api-Token': process.env.CURSEFORGE_TOKEN }, body: form,
  });
  const text = await res.text();
  if (!res.ok) throw new Error(`CurseForge ${res.status} ${text.slice(0, 400)}`);
  console.log(`CurseForge OK ${b.file} ${text.slice(0, 120)}`);
}

for (const b of builds) {
  for (const [site, upload] of [['Modrinth', modrinth], ['CurseForge', curseforge]]) {
    try {
      await upload(b);
    } catch (e) {
      console.error(`${site} FAILED ${b.file}: ${e.message}`);
      failures.push(`${site} ${b.file}`);
    }
  }
}
if (failures.length) {
  console.error(`Failed uploads:\n${failures.join('\n')}`);
  process.exit(1);
}
