package com.grinch.rivo4.controller.util

enum class BlockedNumberMatchType {
    STARTS_WITH,
    EXACT
}

data class BlockedNumberRule(
    val pattern: String,
    val matchType: BlockedNumberMatchType = BlockedNumberMatchType.STARTS_WITH
) {
    fun matches(number: String): Boolean {
        val normalizedPattern = normalizePhoneNumber(pattern)
        if (normalizedPattern.isBlank()) return false

        return normalizeNumberCandidates(number).any { candidate ->
            when (matchType) {
                BlockedNumberMatchType.STARTS_WITH -> candidate.startsWith(normalizedPattern)
                BlockedNumberMatchType.EXACT -> candidate == normalizedPattern
            }
        }
    }

    companion object {
        private fun normalizeNumberCandidates(number: String): Set<String> {
            val normalized = normalizePhoneNumber(number)
            if (normalized.isBlank()) return emptySet()

            val candidates = mutableSetOf(normalized)

            if (normalized.startsWith("90") && normalized.length > 2) {
                candidates.add("0" + normalized.drop(2))
                candidates.add(normalized.drop(2))
            }

            if (normalized.startsWith("0") && normalized.length > 1) {
                candidates.add(normalized.drop(1))
            }

            return candidates.filter { it.isNotBlank() }.toSet()
        }
    }
}