# Gullak · Google Sheet backup (optional, 5 minutes)

Your data normally lives only on your phone. Turn this on to keep a copy in a Google Sheet you own. It's useful if you lose your phone, if you want to see your spending on a laptop, or if you want reviews to show up in the app.

1. Go to **sheets.new** to create a blank Google Sheet. Name it **Gullak backup**.
2. Open **Extensions → Apps Script**.
3. Delete the code that's already there. Paste in everything from [`sheet/Code.gs`](sheet/Code.gs) and click **Save** (💾).
4. Click **Deploy → New deployment**. Click the ⚙ next to "Select type" and choose **Web app**.
   - Execute as: **Me**
   - Who has access: **Anyone**
5. Click **Deploy**, then **Authorize access**. Pick your Google account, then **Advanced → Go to (unsafe) → Allow**. The warning appears because you wrote this script yourself and Google hasn't reviewed it.
6. Copy the **Web app URL**. It ends in `/exec`.
7. In the app, go to **Settings → Google Sheet backup**, paste the link, and tap **Save**.

Your data shows up in the sheet's **Entries**, **Subscriptions**, **Loans** and **Settings** tabs, and updates a few seconds after every change.

**Reviews:** any row added to the **Reviews** tab (columns: id, title, type, period, date, score, summary, fix, body) appears on the app's Reviews screen. Claude can write these for you every month.

**Privacy:** the link works like a password. Anyone who has it can overwrite your backup, so don't share it.
