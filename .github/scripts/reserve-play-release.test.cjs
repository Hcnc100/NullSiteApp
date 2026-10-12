const test = require('node:test');
const assert = require('node:assert/strict');
const reserve = require('./reserve-play-release.cjs');
function fixture(existing = false) {
  const state = { existing, creates: 0, output: undefined };
  return { state, args: {
    context: { repo: { owner: 'test', repo: 'test' }, sha: 'abc' }, versionName: '5.2.0',
    core: { setOutput: (key, value) => { state.output = value; }, notice: () => {} },
    github: { rest: { git: {
      getRef: async () => { if (!state.existing) throw Object.assign(new Error('missing'), { status: 404 }); },
      createRef: async (value) => { assert.equal(value.ref,'refs/tags/play-production-reserved/v5.2.0'); state.creates++; state.existing = true; }
    } } }
  } };
}
test('first attempt reserves; rerun never uploads twice', async () => {
  const { state, args } = fixture(); await reserve(args); assert.equal(state.output,'true');
  await reserve(args); assert.equal(state.output,'false'); assert.equal(state.creates,1);
});
test('other commit using same version is skipped', async () => {
  const { state, args } = fixture(true); args.context.sha = 'other';
  await reserve(args); assert.equal(state.output,'false'); assert.equal(state.creates,0);
});
test('concurrent reservation winner makes loser skip', async () => {
  const { state, args } = fixture();
  args.github.rest.git.createRef = async () => { state.existing = true; throw Object.assign(new Error('race'),{status:422}); };
  await reserve(args); assert.equal(state.output,'false');
});
test('authorization errors fail closed', async () => {
  const { state, args } = fixture(); args.github.rest.git.getRef = async () => { throw Object.assign(new Error('denied'),{status:403}); };
  await assert.rejects(reserve(args),/denied/); assert.notEqual(state.output,'true');
});
test('failed create never authorizes upload', async () => {
  const { state, args } = fixture(); args.github.rest.git.createRef = async () => { throw Object.assign(new Error('unavailable'),{status:503}); };
  await assert.rejects(reserve(args),/unavailable/); assert.notEqual(state.output,'true');
});
test('invalid version never contacts GitHub', async () => {
  const { state, args } = fixture(); args.versionName = '../invalid';
  await assert.rejects(reserve(args),/invalid versionName/); assert.equal(state.creates,0);
});
