# System Pre-requisite

1. Make sure UiTMESamanTrafik-Admin is already running & are able to be connected through port forwarding (ngrok).

# Installation & Running the application

1. Open file in `app > src > main > res > values > strings.xml`.
2. Replace `{{baseUrl}}` string with the `UiTMESamanTrafik-Admin` URL (port forwarded). For example, replace `{{baseUrl}}/polis/form/laporan/maklum-balas` to `http://your-port-forwarded-localhost-url/polis/form/laporan/maklum-balas`.
3. Run application in Android Studio.
