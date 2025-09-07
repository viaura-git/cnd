#!/bin/bash

set -e

kubectl delete -f k8s/addons
kubectl delete ns cnd-dev
kubectl delete ns istio-system