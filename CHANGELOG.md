# Changelog

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
