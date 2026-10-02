# TriggerBot Pro

Minecraft **1.21 + Fabric** için istemci tarafında çalışan, yalnızca **tek oyunculu dünyalarda** etkinleşen gelişmiş TriggerBot projesi.

> Bu repo kaynak kodu içerir. GitHub Actions, her `main` push'unda projeyi Java 21 + Gradle 8.9 ile derler ve oluşan JAR'ı workflow artifact'i olarak yayınlar.

## ✨ Özellikler

### 🎯 TriggerBot
- G tuşu ile aç / kapat
- Sağ Shift ile ayarlar
- H tuşu ile sıradaki profile geç
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
- **CPS modu**: bekleme çubuğu yok sayılır, hız CPS ayarına göre olur; HUD'da gerçek CPS sayacı
- Minimum / maksimum CPS
- Tepki gecikmesi min / max
- Rastgele tepki gecikmesi
- Jitter
- İnsan gibi davran (ritim kayması, duraksama, ara sıra ıska, doğal tepki)
- Havada düşüşü bekle
- Kritik vuruş modu (düşerken, koşmadan, bekleme dolu; ilk uygun tick'te vurur)
- Vuruş için bekleme doluluğu (%90–%100)

### 🛠️ Diğer ayarlar
- Yalnızca kılıç / balta
- Eşya kullanırken duraklat
- Sprintte saldırı davranışı
- El sallama animasyonu
- HUD aç / kapat
- Hedef adı
- Hedef mesafesi
- Vuruş sayacı

### 🎛️ Hazır profiller (menü → PROFİLLER, tek tıkla geçiş)
- **Yumuşak**: rahat tempo, insansı gecikme
- **Dengeli**: varsayılan
- **NetPot**: 1.9+ PvP tarzı, bekleme dolunca vurur, havadaysa düşüşü bekler, insan gibi
- **Hızlı**: CPS modu, 10-14 CPS
- **Kritik**: sadece düşerken, pencere açılınca anında vurur
- **Kritik Doğal**: kritik pencerede kısa insansı gecikmeyle vurur
- **Savaşçı**: saldırı tuşu basılıyken, kılıç/balta ile
- **Mob Avcısı**: CPS modu, sadece düşman mobları, oyunculara dokunmaz
- **Hassas Yardım**: sen saldırı tuşuna basarken en doğru anda tetikler

> Kritik için: zıpla (boşluk), koşma (sprint) ve düşerken vur. Minecraft koşarken kritik vermez.
> Mod yalnızca tek oyunculu dünyalarda çalışır; çok oyunculu veya LAN'a açılmış dünyada kilitlenir.

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
build.sh
gradle.properties
settings.gradle
README.md
LICENSE
```

`TriggerBotPro-GitHub/TriggerBotPro2.0/` gibi ekstra bir üst klasör oluşmamalı.

## 🔨 Yerel derleme

### Linux / macOS / Git Bash

```bash
chmod +x build.sh
./build.sh
```

Çıktı:

```text
build/libs/triggerbotpro-2.2.0.jar
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
