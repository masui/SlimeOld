VERSIONCODE=27
VERSION=1.6.1

# 1.6.1: 高速タップがブレたとき子音入力モードでなくなってしまう問題を解決
# 1.5.1: Google日本語入力を利用
# 1.5.2: ブラウザURLが消えるのを修正
#        候補がないときひらがな/カタカナを表示
#        キー入力で検索キャンセルを徹底

all:
	sed -e "s/VERSIONCODE/${VERSIONCODE}/" AndroidManifest.template | sed -e "s/VERSION/${VERSION}/" > AndroidManifest.xml
	ant debug
install:
	adb install -r bin/Slime-debug.apk
uninstall:
	adb uninstall com.pitecan.slime
debug:
	adb logcat | grep Slime
clean:
	/bin/rm -r -f bin
push:
	git push pitecan.com:/home/masui/git/Slime.git
	git push git@github.com:masui/Slime.git

# 署名してアップロード
publish: clean
	sed -e "s/VERSIONCODE/${VERSIONCODE}/" AndroidManifest.template | sed -e "s/VERSION/${VERSION}/" > AndroidManifest.xml
	ant release
	/bin/cp bin/Slime-release-unsigned.apk bin/Slime.apk
	jarsigner -J-Dfile.encoding=UTF8 -keystore ~/.android/masui.keystore -verbose bin/Slime.apk pitecan
	-/bin/rm bin/Slime-aligned.apk
	zipalign -v 4 bin/Slime.apk bin/Slime-aligned.apk
	scp bin/Slime.apk pitecan.com:/www/www.pitecan.com/Slime
	scp bin/Slime.apk pitecan.com:/www/www.pitecan.com/Slime/Slime-${VERSION}.apk

# Gyazz.com/SlimeDictから辞書を作成!
#
dict:
	cd ../SlimeDict; make; cd ../Slime
	cp ../SlimeDict/dict.txt assets/dict.txt

# Gyazz.com/kdict から辞書を作成!
dict-old:
	ruby -I~/GyazzDict ~/GyazzDict/gyazz2dic 'kdict::リスト' 'kdict::名詞' 'kdict::固有名詞' 'kdict::増井リスト' > /tmp/tmp
	/bin/rm -r -f assets
	mkdir assets
	ruby -I~/GyazzDict ~/GyazzDict/connection2txt /tmp/tmp > assets/dict.txt
