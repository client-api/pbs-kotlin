// Example: open a terminal session against a QEMU VM.
//
// Run with:
//
//   PBS_HOST=https://pbs.example.com:8007 \
//   PBS_TOKEN='PBSAPIToken=root@pam!auto=...' \
//   PBS_NODE=orca PBS_VMID=100 \
//   ./gradlew run -PmainClass=examples.TerminalKt
package examples

import com.clientapi.pbs.TerminalTarget
import com.clientapi.pbs.connectTerminal
import com.clientapi.pbs.infrastructure.ApiClient

fun main() {
    val host = System.getenv("PBS_HOST") ?: "https://localhost:8007"
    ApiClient.apiKey["Authorization"] = System.getenv("PBS_TOKEN") ?: ""

    val node = System.getenv("PBS_NODE") ?: "pbs1"
    val vmid = (System.getenv("PBS_VMID") ?: "100").toInt()
    val baseUrl = "$host/api2/json"

    println("Opening terminal on $node:qemu/$vmid...")
    val session = connectTerminal(
        baseUrl = baseUrl,
        target = TerminalTarget.Qemu(node = node, vmid = vmid),
        onMessage = { print(it) },
        onClose = { code, reason -> println("\n[closed: $code $reason]") },
        onError = { e -> System.err.println("\n[error: $e]") },
    )

    session.resize(120, 32)
    session.send("uname -a\n")

    Thread.sleep(5_000)
    session.close()
}
