# Gullak 🪙

A simple money tracker for India. Set a monthly budget, log spending in seconds, catch UPI payments straight from your bank SMS, and see where your money actually goes.

## Get the app

**Android:** open the [latest release](../../releases/latest) on your phone, tap **Gullak.apk**, and install it. If Chrome asks, allow "Install unknown apps" first.

**iPhone or any browser:** open the web version at **https://asimsahilk-debug.github.io/money-app-Gullak/**, then use **Share → Add to Home Screen** (iPhone) or **⋮ → Add to Home screen** (Android Chrome). The web version can't read SMS, but you can paste messages in.

Updates: the app tells you when a new version is out. Install it over the old one and your data stays.

## What it does
- **Home:** left to spend this month, safe to spend today, income kept separate, entries grouped by day
- **Add:** number pad, most-used shortcuts, suggestions as you type, your own categories, need/want, business/personal, how you paid, receipt photos
- **Automatic SMS (Android):** finds bank/UPI payment messages and asks you to confirm each one. OTPs, offers and bill reminders are ignored. Messages never leave the phone.
- **Insights:** spending by category with limits, wants %, no-spend days, how you paid, last 6 months, biggest expenses
- **Subscriptions:** monthly and yearly totals, renewal countdowns, auto-logging, and "you barely use this" savings
- **Reviews:** copy a month summary into Claude for honest feedback, or have reviews written into your Google Sheet
- **Lent and borrowed, backup and restore, CSV export and import, optional [Google Sheet backup](SETUP-SHEET.md)**

## Privacy
Your data is stored only on your phone, unless you turn on Google Sheet backup, which goes to your own sheet. There are no accounts, no servers, and no tracking.

## How it's built
- `www/index.html` is the whole app (HTML/CSS/JS, no framework)
- `android/` holds the Android wrapper ([Capacitor](https://capacitorjs.com)), with a small native SMS reader at `android/app/src/main/java/in/gullak/app/SmsReaderPlugin.java`
- `.github/workflows/build.yml` runs on every push to `main`: it builds a signed APK, publishes it as a new release, and updates the web version on GitHub Pages
- `sheet/Code.gs` is the optional Google Sheet backup script

To change the app, edit `www/index.html` and push to `main`. GitHub builds and publishes the new version in about 10 minutes.
