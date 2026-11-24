/**
 * Gullak → Google Sheet backup.
 * Paste this into Extensions → Apps Script of a new Google Sheet, then Deploy → New deployment → Web app.
 * The app sends your data here; anything written in the "Reviews" tab shows up in the app.
 */
const REVIEW_COLS = ['id', 'title', 'type', 'period', 'date', 'score', 'summary', 'fix', 'body'];

function doPost(e) {
  const d = JSON.parse(e.postData.contents || '{}');
  if (d.action === 'sync') {
    const cat = (k) => k;
    writeTab('Entries', ['Date', 'Item', 'Amount', 'Type', 'Category', 'Need/Want', 'Business/Personal', 'Paid via', 'Source', 'Month', 'id'],
      (d.entries || []).sort((a, b) => String(a.date).localeCompare(String(b.date)))
        .map(x => [x.date, x.n, x.a, x.t === 'i' ? 'Income' : 'Expense', cat(x.c), x.nw || '', x.bp || '', x.pay || '', x.src || '', x.month, x.id]));
    writeTab('Subscriptions', ['Name', 'Cost', 'Billing', 'Next payment', 'Category', 'Still using', 'Auto-add', 'Active', 'Business/Personal', 'id'],
      (d.subs || []).map(s => [s.name, s.cost, s.billing === 'y' ? 'Yearly' : 'Monthly', s.next || '', s.cat || '', s.using || 'Yes', !!s.auto, s.active !== false, s.bp || '', s.id]));
    writeTab('Loans', ['Person', 'Amount', 'Direction', 'Due', 'Settled', 'id'],
      (d.loans || []).map(l => [l.person, l.a, l.dir, l.due || '', !!l.settled, l.id]));
    writeTab('Settings', ['Key', 'Value'], [
      ['name', d.name || ''], ['budget', (d.settings || {}).budget || ''],
      ['limits', JSON.stringify((d.settings || {}).limits || {})],
      ['customCats', JSON.stringify((d.settings || {}).customCats || [])],
      ['monthBudgets', JSON.stringify((d.settings || {}).monthBudgets || {})],
      ['lastSync', new Date().toISOString()], ['appVersion', d.build || '']]);
    reviewsTab();
  }
  return ContentService.createTextOutput('ok');
}

function doGet(e) {
  const out = { reviews: [] };
  if ((e.parameter || {}).action === 'reviews') {
    const sh = reviewsTab();
    const v = sh.getDataRange().getValues();
    for (let i = 1; i < v.length; i++) {
      if (!v[i][1]) continue;
      const r = {};
      REVIEW_COLS.forEach((c, j) => r[c] = v[i][j] instanceof Date ? Utilities.formatDate(v[i][j], 'Asia/Kolkata', 'yyyy-MM-dd') : String(v[i][j]));
      if (!r.id) r.id = 'r' + i;
      out.reviews.push(r);
    }
  }
  return ContentService.createTextOutput(JSON.stringify(out)).setMimeType(ContentService.MimeType.JSON);
}

function writeTab(name, header, rows) {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  const sh = ss.getSheetByName(name) || ss.insertSheet(name);
  sh.clearContents();
  sh.getRange(1, 1, 1, header.length).setValues([header]).setFontWeight('bold');
  if (rows.length) sh.getRange(2, 1, rows.length, header.length).setValues(rows);
  sh.setFrozenRows(1);
}

function reviewsTab() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  let sh = ss.getSheetByName('Reviews');
  if (!sh) {
    sh = ss.insertSheet('Reviews');
    sh.getRange(1, 1, 1, REVIEW_COLS.length).setValues([REVIEW_COLS]).setFontWeight('bold');
    sh.setFrozenRows(1);
  }
  return sh;
}
