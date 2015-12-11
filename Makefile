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

## GIT

release-remote:
	## add a new remote for public releases
	git remote add github-mirror git@github.com:juniper-project/sched-advisor.git
	## fetch updates from the remote for public releases
	git fetch github-mirror
	## checkout a release branch from the remote for public releases
	git checkout -b release github-mirror/release

release-init:
	## copy the master branch into a new release branch without history and switch into this branch
	git checkout --orphan release
	## commit the content of the release branch
	#git commit -m 'The first public release.'
	## push the release branch into a public repository of releases
	#git push --set-upstream github-mirror release

release-prepare:
	## merge the master branch into a current branch without history
	git merge master --squash
	## resolve conflict by overwriting local by the master branch modifications
	git checkout --theirs .
	## add all modifications caused by the merging into a new commit
	git add .
	## commit the result of merging
	#git commit -m 'Merged with the master (development) branch to fix several bugs.'
	## push into a public repository as a new release
	#git push

mvn-repo-clone:
	## clone a public mvn repository into a temporary location to be ready for deployment
	git clone git@github.com:juniper-project/mvn-repo.git target/mvn-repo
	## add all modifications caused by the mvn repository update
	#git add .
	## commit the result
	#git commit -m 'Updated artefacts of sched-advisor project to fix several bugs.'
	## push into a public mvn repository as a new release
	#git push
