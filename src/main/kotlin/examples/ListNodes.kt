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

import com.clientapi.pbs.Pve
import com.clientapi.pbs.infrastructure.ApiClient

fun main() {
    val host = System.getenv("PBS_HOST") ?: "https://localhost:8007"
    ApiClient.apiKey["Authorization"] = System.getenv("PBS_TOKEN") ?: ""

    val pbs = Pve(basePath = "$host/api2/json")
    val response = pbs.nodes().nodesGetNodes()
    val nodes = response.data ?: emptyList()
    println("Found ${nodes.size} node(s):")
    for (n in nodes) {
        println("  - ${n.node} (status=${n.status}, cpu=${n.cpu}, mem=${n.mem}/${n.maxmem})")
    }
}
