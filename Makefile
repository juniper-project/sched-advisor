PROJECT=juniper-sa
MAVEN=mvn-3.0
SVNDIR=~/Repositories/bigdatajava/work/wp3/d3_9/juniper-sa

test:
	$(MAVEN) test

compile:
	$(MAVEN) compile

build: package

package:
	$(MAVEN) package

deploy:
	$(MAVEN) deploy

site:
	$(MAVEN) site:site
	$(MAVEN) site:deploy

clean:
	$(MAVEN) clean

update:
	find $(shell pwd) -type d -name '.svn' -execdir svn up \;

svn-release:
	rsync -avz --exclude '.git*' --exclude '*/.git*' --exclude '*/.svn' --exclude 'target' --exclude '*/target' --exclude '*.working' --exclude '*~*' $(shell pwd)/ \
	$(SVNDIR)/$(shell git show -s --format=%ci HEAD | cut -d ' ' -f 1 | tr -d '-')-$(shell git show -s --format=%h HEAD)
	git log --pretty=format:"commit %h%nDate: %ci%nAuthor: %an <%ae>%n%s%n" > $(SVNDIR)/SnapshotNotes.txt

svn-dev:
	rsync -avz --exclude '.git*' --exclude '*/.git*' --exclude '*/.svn' --exclude 'target' --exclude '*/target' --exclude '*.working' --exclude '*~*' $(shell pwd)/ \
	$(SVNDIR)/dev
	git log --pretty=format:"commit %h%nDate: %ci%nAuthor: %an <%ae>%n%s%n" > $(SVNDIR)/SnapshotNotes.txt

jars: package
	mkdir -p target
	cp -vr */target/*.jar target
