all:
	ant debug
install:
	adb install -r bin/Slime-debug.apk
uninstall:
	adb uninstall com.pitecan.slime
debug:
	adb logcat | grep Slime
clean:
	/bin/rm -r -f bin/classes
update:
	git update
push:
	git push pitecan.com:/home/masui/git/Slime.git
	git push git@github.com:masui/Slime.git

publish: sign
	scp bin/Slime.apk pitecan.com:/www/www.pitecan.com/tmp
sign:
	ant release
	cp bin/Slime-unsigned.apk bin/Slime.apk
	jarsigner -J-Dfile.encoding=UTF8 -keystore ~/.android/masui.keystore -verbose bin/Slime.apk pitecan

# Gyazz.com/kdict から辞書を作成!
#
dict:
	ruby -I~/GyazzDict ~/GyazzDict/gyazz2dic 'kdict::リスト' 'kdict::名詞' 'kdict::固有名詞' 'kdict::増井リスト' > /tmp/tmp
	/bin/rm -r -f assets
	mkdir assets
	ruby -I~/GyazzDict ~/GyazzDict/connection2txt /tmp/tmp > assets/dict.txt

