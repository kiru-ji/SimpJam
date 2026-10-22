package com.maxrave.simpmusic.ui.navigation.destination.home

import kotlinx.serialization.Serializable

/**
 * [roomCode] is non-null when we arrive from a join link (`simpmusic://lt?room=CODE`): the screen
 * prefills the code field so Join is one tap away. Null for plain navigation, which opens the
 * create/join form untouched.
 */
@Serializable
data class ListenTogetherDestination(
    val roomCode: String? = null,
)
