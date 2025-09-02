# scripts/deploy-cnd-dev-full.ps1
# IntelliJ 클릭 한 번으로 cnd-dev 전체 배포 + Gateway API CRD 설치 + Port-Forward

# 1️⃣ Gateway API CRD 확인/설치
Write-Host "==============================="
Write-Host "🚀 Step 1: Gateway API CRD 확인/설치"
Write-Host "==============================="
if (-not (kubectl get crd gateways.gateway.networking.k8s.io -ErrorAction SilentlyContinue)) {
    Write-Host "👉 Gateway API CRD not found. Installing..."
    kubectl kustomize "github.com/kubernetes-sigs/gateway-api/config/crd?ref=v1.3.0" | kubectl apply -f -
    Write-Host "✅ Gateway API CRD installed successfully"
} else {
    Write-Host "✅ Gateway API CRD already installed"
}
# 1️⃣ istio-system 설치
Write-Host "==============================="
Write-Host "🚀 Step 2: Istio System install"
Write-Host "==============================="
istioctl install -f k8s/01.demo-profile-no-gateways.yaml -y

# 2️⃣ Namespace 적용
Write-Host "==============================="
Write-Host "🚀 Step 3: Namespace 적용"
Write-Host "==============================="
kubectl apply -f k8s/02.namespace.yaml
kubectl label namespace cnd-dev istio-injection=enabled

# 3️⃣ Deployments + Services 적용
Write-Host "==============================="
Write-Host "🚀 Step 4: Deployments + Services 적용"
Write-Host "==============================="
kubectl apply -f k8s/03.deployments.yaml

# 4️⃣ Istio Gateway + HTTPRoute 적용
Write-Host "==============================="
Write-Host "🚀 Step 5: Istio Gateway + HTTPRoute 적용"
Write-Host "==============================="
kubectl apply -f k8s/04.cnd-ingress-gateway.yaml

kubectl get gateway -n cnd-dev

# 6️⃣ Port-Forward
Write-Host "==============================="
Write-Host " Step 6: gateway annotate (because docker has no loadbalancer)"
Write-Host "==============================="
kubectl annotate gateway cnd-ingress-gateway networking.istio.io/service-type=ClusterIP --namespace=cnd-dev

# 5️⃣ Deployment 재시작 (선택)
Write-Host "==============================="
Write-Host " Step 7: Deployment restart"
Write-Host "==============================="
kubectl rollout restart deployment -n cnd-dev
Write-Host "All Deployment restart complete"

# 6️⃣ Port-Forward
Write-Host "==============================="
Write-Host " Step 8: install addons"
Write-Host "==============================="
kubectl apply -f k8s/addons


# 6️⃣ Port-Forward
Write-Host "==============================="
Write-Host " Step 9: Port-Forward cnd-com-gateway:8080 -> localhost:80"
Write-Host " kubectl port-forward svc/cnd-ingress-gateway-istio 8080:80 -n cnd-dev "
Write-Host " test :  http://localhost:8080/api/members/v1/23 "
Write-Host " do in Powershell  "
Write-Host "==============================="
#Start-Process powershell -ArgumentList "-NoExit", "-Command", "kubectl port-forward svc/cnd-ingress-gateway-istio 8080:80 -n cnd-dev"
#Write-Host "Port-Forward 실행 중 (새 PowerShell 창)"