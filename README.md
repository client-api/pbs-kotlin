# pbs-kotlin

Kotlin SDK for the Proxmox Backup Server API. Generated
from the upstream `apidoc.js` from Proxmox Backup Server via [openapi-generator-cli][gen] with
custom Mustache template overrides.

> **Not an official Proxmox project.** Community SDK derived from the
> upstream `apidoc.js`. Always verify against the upstream API viewer.
> <https://pbs.proxmox.com/>.

Targets the JVM via OkHttp 4 + Moshi. Requires JDK ≥ 21 (the
artifact is compiled with the Kotlin 2.2 toolchain targeting JVM 21
bytecode; older JDKs cannot load it). Gradle 8 + the
`foojay-resolver-convention` plugin can auto-provision JDK 21 if your
local toolchain is older.

## Install

Published to **GitHub Packages** on every GitHub release. Configure
the GitHub Packages Maven repo in your build (with a personal access
token that has `read:packages` scope), then add the dependency:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/client-api/pbs-kotlin")
        credentials {
            username = System.getenv("GITHUB_ACTOR") ?: providers.gradleProperty("gpr.user").get()
            password = System.getenv("GITHUB_TOKEN") ?: providers.gradleProperty("gpr.token").get()
        }
    }
}

dependencies {
    implementation("com.clientapi:pbs:2026.5.23")
}
```

Or build locally:

```bash
./gradlew build
./gradlew publishToMavenLocal
```

## Usage

```kotlin
import com.clientapi.pbs.Pve
import com.clientapi.pbs.infrastructure.ApiClient

// Configure the shared OkHttp + auth headers (set once, reused everywhere).
ApiClient.apiKey["Authorization"] = "PBSAPIToken=user@realm!tokenid=uuid-secret"

val pbs = Pve(basePath = "https://pbs1.example.com:8007/api2/json")

// Per-tag accessors are lazily instantiated and share the same basePath + Call.Factory.
val status = pbs.qemu().qemuVmStatus(node = "pbs1", vmid = 100L)
val nodes  = pbs.nodes().nodesGetNodes()
```

The unified `Pbs` class wraps each per-tag API class (`QemuApi`,
`LxcApi`, `ClusterApi`, `NodesApi`, …) so consumers don't need to
instantiate them individually.

## Compound configs

PVE encodes many fields as CLI-style shorthand strings
(`net0=virtio,bridge=vmbr0,firewall=1`). Round-trip helpers will be
emitted for every compound config schema in a future iteration. For
now, build the string manually and pass through the relevant API.

## Indexed families

Numbered properties (`net0..net31`, `mp0..mp255`, …) are exposed via
`getNets()` / `withNets(map)` extension functions on every model.
The per-index data class fields are annotated
`@Deprecated(level = DeprecationLevel.HIDDEN)`, so direct access is a
compile error and IDE autocomplete only surfaces the collapsed view:

```kotlin
val req = QemuCreateVmRequest().withNets(mapOf(
    0 to PveQemuNetField(string = "virtio,bridge=vmbr0"),
    3 to PveQemuNetField(string = "e1000,bridge=vmbr1"),
))
// Wire format: { "net0": "virtio,bridge=vmbr0", "net3": "e1000,bridge=vmbr1" }
```

## License

Apache 2.0 — see [LICENSE](./LICENSE).

[gen]: https://openapi-generator.tech
