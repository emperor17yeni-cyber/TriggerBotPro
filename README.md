# TriggerBot Pro

Minecraft **1.21 + Fabric** için istemci tarafında çalışan, yalnızca **tek oyunculu dünyalarda** etkinleşen gelişmiş TriggerBot projesi.

> Bu repo kaynak kodu içerir. GitHub Actions, her `main` push'unda projeyi Java 21 + Gradle 8.9 ile derler ve oluşan JAR'ı workflow artifact'i olarak yayınlar.

## ✨ Özellikler

### 🎯 TriggerBot
- G tuşu ile aç / kapat
- Sağ Shift ile ayarlar
- Hedefi görüş hizasında kontrol etme
- Maksimum menzil: **1.0–6.0 blok**
- Sadece tek oyunculu dünyalarda çalışma
- Saldırı bekleme süresini kontrol etme

### 👤 Hedef filtreleri
- Oyuncular
- Düşman yaratıklar
- Pasif yaratıklar
- Nötr yaratıklar
- Yaratıcı oyuncuları yok say
- Görünmezleri yok say
- Ölü hedefleri yok say
- Duvar arkasına saldırmama

### ⚡ Zamanlama
- Minimum / maksimum CPS
- Tepki gecikmesi min / max
- Rastgele tepki gecikmesi
- Jitter
- Kritik vuruş modu

### 🛠️ Diğer ayarlar
- Yalnızca kılıç / balta
- Eşya kullanırken duraklat
- Sprintte saldırı davranışı
- El sallama animasyonu
- HUD aç / kapat
- Hedef adı
- Hedef mesafesi
- Vuruş sayacı

### 🎛️ Hazır profiller
- **Yumuşak**
- **Dengeli**
- **Hızlı**
- **Kritik**

Ayarlar `.minecraft/config/triggerbotpro.json` dosyasına kaydedilir.

## 📁 Proje yapısı

```text
TriggerBotPro/
├── .github/
│   └── workflows/
│       └── build.yml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/emperor/triggerbot/
│       │       ├── client/
│       │       │   └── TriggerBotProClient.java
│       │       ├── config/
│       │       │   └── TriggerConfig.java
│       │       └── screen/
│       │           └── TriggerConfigScreen.java
│       └── resources/
│           ├── assets/triggerbotpro/lang/
│           │   ├── en_us.json
│           │   └── tr_tr.json
│           └── fabric.mod.json
├── .gitattributes
├── .gitignore
├── build.bat
├── build.gradle
├── build.sh
├── gradle.properties
├── LICENSE
├── README.md
└── settings.gradle
```

## 🚀 GitHub'a yükleme

GitHub'daki `TriggerBotPro` repo sayfasında **Add file → Upload files** ekranını aç.

Bu ZIP'i çıkardıktan sonra, **ZIP'in içindeki dosyaların tamamını** yükle. Yani GitHub'da dosya ağacının en üstünde doğrudan şunları görmelisin:

```text
.github/
src/
.gitignore
.gitattributes
build.gradle
build.bat
build.sh
gradle.properties
settings.gradle
README.md
LICENSE
```

`TriggerBotPro-GitHub/TriggerBotPro2.0/` gibi ekstra bir üst klasör oluşmamalı.

## 🔨 Yerel derleme

### Windows

Java 21 kurulu olmalı. Ardından:

```text
build.bat
```

### Linux / macOS

```bash
chmod +x build.sh
./build.sh
```

Çıktı:

```text
build/libs/triggerbotpro-2.0.0.jar
```

## 🤖 GitHub Actions

Repoya push yaptığında `.github/workflows/build.yml` otomatik olarak çalışır.

Başarılı build'den sonra:

**GitHub → Actions → Build TriggerBot Pro → ilgili çalışma → Artifacts**

bölümünden JAR artifact'ini alabilirsin.

## 📦 Gereksinimler

- Minecraft **1.21**
- Fabric Loader **0.15.11 veya üzeri**
- Fabric API **0.102.0+1.21 veya üzeri**
- Java **21+**
- Gradle **8.9** (yerel yardımcı scriptleri bunu kullanır)

## 📄 Lisans

MIT License. Ayrıntılar için `LICENSE` dosyasına bak.
