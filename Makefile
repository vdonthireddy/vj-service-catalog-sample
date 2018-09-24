run:
	mvn clean install

docker:
	docker rmi -f pgclient
	docker build -t pgclient .
	docker tag pgclient vdonthireddy/pgclient:5.0
	docker push vdonthireddy/pgclient:5.0

deploy:
	kubectl delete deploy pgclient-deployment & kubectl delete service pgclient-service
	kubectl apply -f aks.yml

display:
	kubectl get po,deploy,svc
	kubectl get svc -w
