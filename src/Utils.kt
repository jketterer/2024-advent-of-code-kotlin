import com.soberg.kotlin.aoc.api.AdventOfCodeInputApi
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.readText

/**
 * Reads and caches input from https://adventofcode.com
 */
fun readInput(year: Int, day: Int) = AdventOfCodeInputApi(
    cachingStrategy = AdventOfCodeInputApi.CachingStrategy.LocalTextFile("input")
).blockingReadInput(
    year = year, // e.g. 2015-2024
    day = day, // e.g. 1-24
    sessionToken = readSessionToken(), // e.g. "21216c7...314d"
).getOrThrow()

/**
 * Reads local session token from secret file
 */
private fun readSessionToken(): String {
    val secretTokenFile = Path("session-token.secret")
    require(secretTokenFile.exists()) {
        "session-token.secret file must exist and contain the sessionToken for Advent of Code"
    }
    return secretTokenFile.readText().trim()
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

