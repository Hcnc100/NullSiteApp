// A reservation is NOT a publication receipt. Keep it on uncertain/failed uploads:
// reconcile Play manually before removing it. Never automatically retry upload.
module.exports = async function reserve({ github, context, core, versionName }) {
  if (!/^\d+\.\d+\.\d+(?:-[A-Za-z0-9.-]+)?$/.test(versionName || '')) {
    throw new Error('Missing or invalid versionName; refusing production upload');
  }
  const ref = `tags/play-production-reserved/v${versionName}`;
  const params = { ...context.repo, ref };
  const exists = async () => {
    try { await github.rest.git.getRef(params); return true; }
    catch (error) { if (error.status === 404) return false; throw error; }
  };
  const skip = () => {
    core.setOutput('publish', 'false');
    core.notice(`Version ${versionName} already reserved: upload skipped. Check Play before any manual recovery.`);
  };
  if (await exists()) { skip(); return; }
  try {
    await github.rest.git.createRef({ ...context.repo, ref: `refs/${ref}`, sha: context.sha });
  } catch (error) {
    // Atomic create arbitrates parallel runs. Only a verified existing ref can skip.
    if ((error.status === 409 || error.status === 422) && await exists()) { skip(); return; }
    throw error;
  }
  core.setOutput('publish', 'true');
  core.notice(`Reserved ${versionName}; this run alone may upload it to production.`);
};
