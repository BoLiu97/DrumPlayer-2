package com.example.drumplayer

import java.util.*
import kotlin.collections.ArrayList

data class Drum (
    var radio: ArrayList<Int> =  arrayListOf(),
    var timestamp: ArrayList<Long>  = arrayListOf(),
    var recordtime: Date = Date(),
    var user: String = "",
    var idU: String = ""

)