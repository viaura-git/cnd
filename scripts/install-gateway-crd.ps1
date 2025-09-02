if (-not (kubectl get crd gateways.gateway.networking.k8s.io -ErrorAction SilentlyContinue)) {
    Write-Host "👉 Gateway API CRD not found. Installing..."
    kubectl kustomize "github.com/kubernetes-sigs/gateway-api/config/crd?ref=v1.3.0" | kubectl apply -f -
} else {
    Write-Host "✅ Gateway API CRD already installed"
}
