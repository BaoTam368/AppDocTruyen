const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '..', '.env') });

const syncService = require('../src/services/syncService');
const databaseService = require('../src/services/databaseService');

const DEFAULT_LIMIT = 200;
const DEFAULT_PAGE_SIZE = 50;

function readNumberArg(names, fallback) {
    const argv = process.argv.slice(2);

    for (let index = 0; index < argv.length; index++) {
        const arg = argv[index];
        const matchedName = names.find((name) => arg === name || arg.startsWith(`${name}=`));
        if (!matchedName) continue;

        const rawValue = arg.includes('=') ? arg.split('=')[1] : argv[index + 1];
        const parsedValue = Number.parseInt(rawValue, 10);
        return Number.isNaN(parsedValue) ? fallback : parsedValue;
    }

    return fallback;
}

async function main() {
    const total = readNumberArg(['--limit'], DEFAULT_LIMIT);
    const pageSize = readNumberArg(['--page-size', '--pageSize'], DEFAULT_PAGE_SIZE);

    console.log(`Starting MangaDex popular sync: ${total} manga, page size ${pageSize}`);
    const mangas = await syncService.syncPopularMangas({ total, pageSize });
    console.log(`Sync completed: ${mangas.length} manga`);
}

main()
    .catch((error) => {
        console.error('Sync failed:', error.message);
        process.exitCode = 1;
    })
    .finally(() => {
        databaseService.closeDatabase();
    });