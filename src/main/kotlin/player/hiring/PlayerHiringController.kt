package player.hiring

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*

@Controller("/player-hiring")
class PlayerHiringController {
    @Post("/{clubId}")
    @Produces(MediaType.TEXT_PLAIN)
    fun show(clubId: String): String {
        return "You are requesting for the $clubId balance"
    }
}
