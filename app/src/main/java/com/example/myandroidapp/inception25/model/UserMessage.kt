import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Parcelize
data class UserMessage(
    val id: Int = Random.nextInt(),
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val fromNotification: Boolean = false
) : Parcelable