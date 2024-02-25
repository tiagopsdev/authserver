package br.pucpr.authserver.files

import java.io.InputStream

data class FileToStream(

    val size: Int,
    val inputStream: InputStream,

)
