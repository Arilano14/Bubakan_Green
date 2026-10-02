package id.bubakangreen.app.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class QrCodeGeneratorTest {

    @Test
    fun isEligibleForQr_whenPublishedAndActive_returnsTrue() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = true, status = "ACTIVE")
        assertThat(result).isTrue()
    }

    @Test
    fun isEligibleForQr_whenPublishedAndPublishedStatus_returnsTrue() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = true, status = "PUBLISHED")
        assertThat(result).isTrue()
    }

    @Test
    fun isEligibleForQr_whenNotPublished_returnsFalse() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = false, status = "ACTIVE")
        assertThat(result).isFalse()
    }

    @Test
    fun isEligibleForQr_whenStatusPendingReview_returnsFalse() {
        val result = QrCodeGenerator.isEligibleForQr(isPublished = true, status = "PENDING_REVIEW")
        assertThat(result).isFalse()
    }

    @Test
    fun isEligibleForQr_whenStatusDraftOrArchived_returnsFalse() {
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "DRAFT")).isFalse()
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "ARCHIVED")).isFalse()
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "REJECTED")).isFalse()
    }

    @Test
    fun isEligibleForQr_caseInsensitiveHandling_returnsTrue() {
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "active")).isTrue()
        assertThat(QrCodeGenerator.isEligibleForQr(isPublished = true, status = "  Active  ")).isTrue()
    }
}
