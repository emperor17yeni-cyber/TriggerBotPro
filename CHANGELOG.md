# Changelog

## 2.2.0

- CPS düzeltmesi: bekleme çubuğu açıkken kılıç saniyede ~1,6 vuruştan fazla vuramadığı için CPS ayarı fiilen yok sayılıyordu. Yeni **CPS modu**: bekleme yok sayılır, hız tamamen CPS ayarına göre olur
- Zamanlayıcı düzeltmesi: 50 ms'lik tick sınırı yüzünden gerçek hız hedefin altına düşüyordu (7–10 hedefi ~6,8 CPS veriyordu); artık hedef ortalamaya oturuyor
- HUD'a gerçek **CPS sayacı** (son 1 saniyedeki tıklama sayısı) ve aktif mod (CPS / Bekleme) eklendi
- Yeni profil: **NetPot** (1.9+ PvP tarzı: bekleme dolunca vurur, havadaysa düşüşü bekler, insan gibi tepki)
- **İnsan gibi davran** seçeneği: ritim zamanla kayar, aralıklar düzgün değil doğal dağılır, arada kısa duraksama ve ara sıra ıska olur, tepki süresi ortada yoğunlaşır
- **Havada düşüşü bekle** seçeneği: hâlâ yükseliyorsan vurmaz, düşüşte (kritik anında) vurur
- Hedefle dövüşürken ilk temas tepkisi her vuruşta tekrarlanmaz
- Mod hiçbir şey loglamaz, her tick'te sadece birkaç karşılaştırma yapar

## 2.1.0

- Kritik vuruş zamanlaması yenilendi: yalnızca Minecraft'ın gerçek kritik koşulları sağlandığında (düşüyor, koşmuyor, yerde/suda/merdivende/körlükte değil) ve bekleme çubuğu en az %95 doluyken vurur
- "Anında vur" seçeneği: kritik penceresi açılan ilk tick'te vurur (tepki gecikmesi ve CPS beklemesi atlanır)
- 8 hazır profil: Yumuşak, Dengeli, Hızlı, Kritik, Kritik Doğal, Savaşçı, Mob Avcısı, Hassas Yardım
- Profil sekmesinden tek tıkla geçiş, oyun içinde H tuşuyla sıradaki profile geçiş
- Profil uygulanınca tüm davranış/zamanlama ayarları birlikte güncellenir (eski profilden ayar kalmaz)
- Vuruş için bekleme doluluğu ayarı (%90–%100)
- Yeni hedefe geçince tepki süresi yeniden başlar
- Tek oyunculu sınırı artık gerçekten uygulanıyor (çok oyunculu / LAN'a açık dünyada kilitli)
- ESC ile menüden çıkınca ayarlar kaybolmuyor
- HUD: aktif profil ve kritik penceresi göstergesi
- GitHub Actions: yanlış JAR yolu düzeltildi

## 2.0.0

- Türkçe sekmeli ayar arayüzü
- Hedef filtreleri
- CPS ve tepki gecikmesi sistemi
- Jitter desteği
- Kritik vuruş modu
- Hazır profiller
- HUD bilgi kartı
- JSON yapılandırma
- Tek oyunculu çalışma sınırı
- GitHub Actions otomatik build
