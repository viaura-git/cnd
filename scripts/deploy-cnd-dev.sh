#!/bin/bash
# scripts/deploy-cnd-dev-full.sh
# IntelliJ 클릭 한 번으로 cnd-dev 전체 배포 + Gateway API CRD 설치 + Port-Forward (macOS/Linux 용)

set -e

# 1️⃣ Gateway API CRD 확인/설치
echo "==============================="
echo "🚀 Step 1: Gateway API CRD 확인/설치"
echo "==============================="
kubectl get crd gateways.gateway.networking.k8s.io &> /dev/null || \
{ kubectl kustomize "github.com/kubernetes-sigs/gateway-api/config/crd?ref=v1.3.0" | kubectl apply -f -; }


# 2️⃣ istio-system 설치
echo "==============================="
echo "🚀 Step 2: Istio System install"
echo "==============================="
istioctl install -f k8s/01.demo-profile-no-gateways.yaml -y

# 3️⃣ Namespace 적용
echo "==============================="
echo "🚀 Step 3: Namespace 적용"
echo "==============================="
kubectl apply -f k8s/02.namespace.yaml
# kubectl label namespace cnd-dev istio-injection=enabled

# 4️⃣ Deployments + Services 적용
echo "==============================="
echo "🚀 Step 4: Deployments + Services 적용"
echo "==============================="
# kubectl apply -f k8s/03.deployments.yaml
kubectl apply -f k8s/03-0.jwt-secret.yaml
# kubectl apply -f k8s/03-1.deployments-api-gateway.yaml
kubectl apply -f k8s/03-2.deployments-auth.yaml
kubectl apply -f k8s/03-3.deployments-lea.yaml
kubectl apply -f k8s/03-4.deployments-res.yaml

# 5️⃣ Istio Gateway + HTTPRoute 적용
echo "==============================="
echo "🚀 Step 5: Istio Gateway + HTTPRoute 적용"
echo "==============================="
kubectl apply -f k8s/04.cnd-ingress-gateway.yaml
kubectl get gateway -n cnd-dev

# 6️⃣ Gateway annotate (docker 환경에서는 LoadBalancer 없음)
echo "==============================="
echo " Step 6: gateway annotate (because docker has no loadbalancer)"
echo "==============================="
kubectl annotate gateway cnd-ingress-gateway networking.istio.io/service-type=ClusterIP --namespace=cnd-dev --overwrite

# 7️⃣ Istio-SpringCloudGateway Authentication
echo "==============================="
echo " Step 7: Istio-SpringCloudGateway Authentications"
echo "==============================="
kubectl apply -f k8s/05.cnd-gateway-authentication.yaml

# 8️⃣ Istio-Authorization Policy (non JWT)
echo "==============================="
echo " Step 8: Istio-Authorization Policy : non JWT"
echo "==============================="
kubectl apply -f k8s/06.cnd-authorization-policy-non-jwt.yaml

# 9️⃣ Istio-Authorization Policy (with JWT)
echo "==============================="
echo " Step 9: Istio-Authorization Policy : with JWT"
echo "==============================="
kubectl apply -f k8s/06.cnd-authorization-policy-with-jwt.yaml

# 🔟 Istio-egress-gateway
echo "==============================="
echo " Step 10: Istio-egress-gateway : 외부 DB (redis, mariadb) 접속"
echo "==============================="
kubectl apply -f k8s/07.cnd-egress-gateway.yaml

# 1️⃣1️⃣ Deployment 재시작
echo "==============================="
echo " Step 11: Deployment restart"
echo "==============================="
#kubectl rollout restart deployment -n cnd-dev
echo "All Deployment restart complete"

# 1️⃣2️⃣ Addons 설치
echo "==============================="
echo " Step 12: install addons"
echo "==============================="
kubectl apply -f k8s/addons

# 1️⃣3️⃣ Port-Forward 안내
echo "==============================="
echo " Step 13: Port-Forward cnd-com-gateway:8080 -> localhost:80"
echo " kubectl port-forward svc/cnd-ingress-gateway-istio 8080:80 -n cnd-dev "
echo " test :  http://localhost:8080/api/members/v1/23 "
echo "==============================="
# 실행하려면 아래 명령 직접 입력하세요:
# kubectl port-forward svc/cnd-ingress-gateway-istio 8080:80 -n cnd-dev
