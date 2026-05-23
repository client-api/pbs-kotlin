// Example: list cluster nodes.
//
// Run with:
//
//   PBS_HOST=https://pbs.example.com:8007 \
//   PBS_TOKEN='PBSAPIToken=root@pam!auto=...' \
//   ./gradlew run -PmainClass=examples.ListNodesKt
//
// Or compile + run with kotlin CLI directly.
package examples

import com.clientapi.pbs.apis.NodesApi
import com.clientapi.pbs.infrastructure.ApiClient

fun main() {
    val host = System.getenv("PBS_HOST") ?: "https://localhost:8007"
    ApiClient.apiKey["Authorization"] = System.getenv("PBS_TOKEN") ?: ""

    // Non-PVE products: the upstream apidoc declares this endpoint
    // `returns: { type: null }`, so `response.data` is untyped. Print
    // the whole response and let the user see what came back.
    val response = NodesApi(basePath = "$host/api2/json").nodesGetNodes()
    println("Response: $response")
}
