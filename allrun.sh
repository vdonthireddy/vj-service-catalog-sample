mvn clean install

docker rmi -f pgclient
docker build -t pgclient .
docker tag pgclient vdonthireddy/pgclient:3.0
docker push vdonthireddy/pgclient:3.0

kubectl delete deploy pgclient-deployment & kubectl delete service pgclient-service
kubectl apply -f aks.yml

kubectl get po,deploy,svc
kubectl get svc -w
