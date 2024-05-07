package com.example.dealdoc.Interfaces

import android.text.TextUtils
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.Signature
import java.security.SignatureException
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class Security {
    private val KEY_FACTORY_ALGORITHM = "RSA"
    private val SIGNATURE_ALGORITHM = "SHA1withRSA"

    @Throws(IOException::class)
    fun verifyPurchase(
        base64Publickey: String?, signedData: String,
        signature: String?): Boolean {
        if (TextUtils.isEmpty(signedData) || TextUtils.isEmpty(base64Publickey) || TextUtils.isEmpty(signature))
        {

            return false
        }
        val key = generatePublicKey(base64Publickey)
        return verify(key, signedData, signature)
    }

    @Throws(IOException::class)
    private fun generatePublicKey(encodedPublicKey: String?): PublicKey {
        return try {
//            val decodedKey = Base64.decode(encodedPublicKey, android.util.Base64.DEFAULT)
            val decodedKey = android.util.Base64.decode(encodedPublicKey, android.util.Base64.DEFAULT)
            val keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        }catch (e: NoSuchAlgorithmException){
            //"RSA" is guarnteed to be available.
            throw RuntimeException(e)
        }catch (e: InvalidKeyException){
            val msg = "Invalid key specification: $e"
            throw IOException(msg)
        }
    }

    private fun verify(publicKey: PublicKey, signedData: String, signature: String?): Boolean {
        val signatureBytes: ByteArray = try {
            android.util.Base64.decode(signature, android.util.Base64.DEFAULT)
        }catch (e: java.lang.IllegalArgumentException){

            return false
        }
        try {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(signedData.toByteArray())

            return signatureAlgorithm.verify(signatureBytes)
        }catch (e: NoSuchAlgorithmException){
            throw RuntimeException(e)
        }catch (e: InvalidKeyException) {

        }catch (e: SignatureException){

        }
        return false
    }
}