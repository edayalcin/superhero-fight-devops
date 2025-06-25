# ArgoCD Installation
ArgoCD minikube içerisinde deploy ettikten sonra kullanmış olduğu servisin type değerini
NodePort olarak set ettim. Sonrasında minikube service ile dışarıdan erişilebilir hale getirdim.

ArgoCD Kurulumu ve NodePort
- kubectl create namespace argocd
- kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
- kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "NodePort"}}'

NodePort değeri
- kubectl get svc argocd-server -n argocd

ArgoCD admin şifresini öğrenmek için;
- kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d && echo

Minikube service info
- minikube service argocd-server --url -n argocd

