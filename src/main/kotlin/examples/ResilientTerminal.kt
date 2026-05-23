// Example: resilient terminal session with auto-reconnect.
//
// Run with:
//
//   PBS_HOST=https://pbs.example.com:8007 \
//   PBS_TOKEN='PBSAPIToken=root@pam!auto=...' \
//   PBS_NODE=orca PBS_VMID=100 \
//   ./gradlew run -PmainClass=examples.ResilientTerminalKt
package examples

import com.clientapi.pbs.RetryOptions
import com.clientapi.pbs.TerminalTarget
import com.clientapi.pbs.connectTerminalResilient
import com.clientapi.pbs.infrastructure.ApiClient

fun main() {
    val host = System.getenv("PBS_HOST") ?: "https://localhost:8007"
    ApiClient.apiKey["Authorization"] = System.getenv("PBS_TOKEN") ?: ""

    val node = System.getenv("PBS_NODE") ?: "pbs1"
    val vmid = (System.getenv("PBS_VMID") ?: "100").toInt()
    val baseUrl = "$host/api2/json"

    val session = connectTerminalResilient(
        baseUrl = baseUrl,
        target = TerminalTarget.Qemu(node = node, vmid = vmid),
        retry = RetryOptions(maxRetries = 20, initialDelayMs = 250),
        onMessage = { print(it) },
        onClose = { code, _ -> println("\n[final close: $code]") },
        onReconnect = { attempt -> println("\n[reconnected after $attempt attempts]") },
        onGiveUp = { err -> System.err.println("\n[retries exhausted: $err]") },
    )

    session.send("date\n")
    val deadline = System.currentTimeMillis() + 5 * 60 * 1000L
    while (System.currentTimeMillis() < deadline) {
        Thread.sleep(30_000)
        session.send("date\n")
    }
    session.close()
}
