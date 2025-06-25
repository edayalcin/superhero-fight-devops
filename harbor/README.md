# Harbor Installation
Minikube içeriside Harbor Registry çalıştıracağımız için öncelikle minikube konfigürasyonlarını tamamlayalım. 

- Harbor'u ingress ile expose edeceğim için minikube içerisinden bunu aktif edelim.
'minikube addons enable ingress'
- Sonrasında Harbor helm paketini ekleyelim
'helm repo add harbor https://helm.goharbor.io'
'helm repo update'
- Harbor ingress ayarları için harbor-values.yaml dosyasını düzenleyelim
- Local üzerinden erişebilmek için /etc/hosts dosyasını düzenleyelim
'127.0.0.1 harbor.local'
- Son olarakta erişimi sağlayabilmek için minikube tunnel aktif edelim
'minikube tunnel'
NOT: işlemler boyunca minikube tunnel aktif olmalıdır.

Daha sonrasında http://harbor.local/ adresinde erişim sağlayailiri.