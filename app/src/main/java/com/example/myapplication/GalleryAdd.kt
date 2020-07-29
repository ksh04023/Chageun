package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.document.FirebaseVisionCloudDocumentRecognizerOptions
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import kotlinx.android.synthetic.main.gallery_add.*
import java.io.IOException
import java.util.*
import kotlin.math.abs


class GalleryAdd : AppCompatActivity() {
    val SELECT_IMAGE = 100
    val itemList = listOf("탄수화물","단백질","포화지방산","지방","나트륨","당류","콜레스테롤","트랜스지방산")
    val itemHash = HashMap<String,String>()
    val foodDetailList = ArrayList<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_add)
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE)
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, SELECT_IMAGE)

        addgallery.setOnClickListener {
            val directintent = Intent(applicationContext,FoodAddDirect::class.java)
            directintent.putStringArrayListExtra("fooddetail",foodDetailList)
            startActivity(directintent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data == null)
            finish()
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                var cameraimage = findViewById<ImageView>(R.id.galleryimage)
                cameraimage.setImageURI(data!!.data)
                imageFromPath(this,data!!.data)
            }
        }
    }

    private fun imageFromPath(context: Context, uri: Uri) {
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(context, uri)
            Toast.makeText(this,"로딩중입니다...", Toast.LENGTH_SHORT).show()
            recognizeTextCloud(image)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // [END image_from_path]
    }

    companion object {
        private val TAG = "MLKIT"
        private val MY_CAMERA_ID = "my_camera_id"
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }


    private fun recognizeTextCloud(image: FirebaseVisionImage) {

        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
            .setLanguageHints(Arrays.asList("en", "hi", "ko"))
            .build()
        // [END set_detector_options_cloud]
        // [START get_detector_cloud]

        val detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)

        // [START run_detector_cloud]
        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                var nutriPattern = Regex("""([가-힣]+)\s*([\d.,]+)\s*(m*g+)""")
                var calPattern = """([0-9.]+)\s*(kcal|kCal|Kcal|KCal|KCAL)""".toRegex()
                lateinit var matchResult: MatchResult
                for (block in firebaseVisionText.textBlocks) {
                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text
                    galleryresult.text = galleryresult.text.toString() + text.toString().replace("\n","")
                    for (line in block.lines) {
                        Log.d("line",line.text)
                        for (element in line.elements) {
                            Log.d("ele",element.text)
                        }
                    }
                }
                val nutriResult = nutriPattern.findAll(galleryresult.text)
                val calResult = calPattern.find(galleryresult.text)
                if (calResult != null) {
                    itemHash["열량"] = calResult.value
                }
                if(nutriResult != null) {
                    for (nutri in nutriResult) {
                        Log.d("regex", nutri.value)
                        for (list in itemList) {
                            val similar = abs(levenshtein(list, nutri.groups[1]?.value.toString()))
                            if (similar == 0) {
                                Log.d("leven", "$list ${nutri.groups[1]?.value.toString()} $similar")
                                itemHash[list] = nutri.groups[2]?.value.toString()
                                break
                            }
                        }
                    }
                }
                foodDetailList.add("")
                foodDetailList.add(itemHash["열량"])
                foodDetailList.add(itemHash["탄수화물"])
                foodDetailList.add(itemHash["단백질"])
                foodDetailList.add(itemHash["지방"])
                foodDetailList.add(itemHash["나트륨"])
                foodDetailList.add(itemHash["당류"])
                foodDetailList.add(itemHash["포화지방산"])
                foodDetailList.add(itemHash["트랜스지방산"])
                foodDetailList.add(itemHash["콜레스테롤"])
            }

            .addOnFailureListener {
            }

        // [END run_detector_cloud]

    }

    private fun getLocalDocumentRecognizer(): FirebaseVisionDocumentTextRecognizer {
        val detector = FirebaseVision.getInstance()
            .cloudDocumentTextRecognizer

        return detector
    }

    private fun getCloudDocumentRecognizer(): FirebaseVisionDocumentTextRecognizer {
        val options = FirebaseVisionCloudDocumentRecognizerOptions.Builder()
            .setLanguageHints(Arrays.asList("en", "hi"))
            .build()
        val detector = FirebaseVision.getInstance()
            .getCloudDocumentTextRecognizer(options)
        return detector
    }

    private fun processDocumentImage() {
        val detector = getLocalDocumentRecognizer()
        val myImage = FirebaseVisionImage.fromByteArray(byteArrayOf(),
            FirebaseVisionImageMetadata.Builder().build())

        // [START mlkit_process_doc_image]

        detector.processImage(myImage)
            .addOnSuccessListener {

            }

            .addOnFailureListener {

            }
    }



    private fun processDocumentTextBlock(result: FirebaseVisionDocumentText) {
        val resultText = result.text
        for (block in result.blocks) {
            val blockText = block.text
            val blockConfidence = block.confidence
            val blockRecognizedLanguages = block.recognizedLanguages
            val blockFrame = block.boundingBox
            for (paragraph in block.paragraphs) {
                val paragraphText = paragraph.text
                val paragraphConfidence = paragraph.confidence
                val paragraphRecognizedLanguages = paragraph.recognizedLanguages
                val paragraphFrame = paragraph.boundingBox
                for (word in paragraph.words) {
                    val wordText = word.text
                    val wordConfidence = word.confidence
                    val wordRecognizedLanguages = word.recognizedLanguages
                    val wordFrame = word.boundingBox
                    for (symbol in word.symbols) {
                        val symbolText = symbol.text
                        val symbolConfidence = symbol.confidence
                        val symbolRecognizedLanguages = symbol.recognizedLanguages
                        val symbolFrame = symbol.boundingBox
                    }
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String, activity: Activity, context: Context): Int {
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
            .getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360

        val result: Int
        when (rotationCompensation) {
            0 -> result = FirebaseVisionImageMetadata.ROTATION_0
            90 -> result = FirebaseVisionImageMetadata.ROTATION_90
            180 -> result = FirebaseVisionImageMetadata.ROTATION_180
            270 -> result = FirebaseVisionImageMetadata.ROTATION_270
            else -> {
                result = FirebaseVisionImageMetadata.ROTATION_0
                Log.e(TAG, "Bad rotation value: $rotationCompensation")
            }
        }
        return result
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getCompensation(activity: Activity, context: Context) {
        val rotation = getRotationCompensation(MY_CAMERA_ID, activity, context)
    }

}
