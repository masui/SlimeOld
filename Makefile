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
#
# Gyazz.com/kdict から辞書を作成!
#
dict:
	ruby -I~/GyazzDict ~/GyazzDict/gyazz2dic 'kdict::リスト' 'kdict::名詞' 'kdict::固有名詞' 'kdict::増井リスト' > /tmp/tmp
	/bin/rm -r -f assets
	mkdir assets
	ruby -I~/GyazzDict ~/GyazzDict/connection2txt /tmp/tmp > assets/dict.txt
