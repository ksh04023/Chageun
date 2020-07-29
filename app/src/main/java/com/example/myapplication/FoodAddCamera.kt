package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import kotlinx.android.synthetic.main.activity_food_add_camera.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class FoodAddCamera : AppCompatActivity() {
    private var imageFilePath: String? = null
    private var photoUri: Uri? = null
    val itemList = listOf("탄수화물","단백질","포화지방산","지방","나트륨","당류","콜레스테롤","트랜스지방산")
    val itemHash = HashMap<String,String>()
    val foodDetailList = ArrayList<String?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_add_camera)
        sendTakePhotoIntent()
        addcamera.setOnClickListener {
            val directintent = Intent(applicationContext,FoodAddDirect::class.java)
            directintent.putStringArrayListExtra("fooddetail",foodDetailList)
            startActivity(directintent)
        }
    }

    private fun imageFromBitmap(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        Toast.makeText(this,"로딩중입니다...",Toast.LENGTH_SHORT).show()
        recognizeTextCloud(image)
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
                    cameraresult.text = cameraresult.text.toString() + text.toString().replace("\n","")
                    for (line in block.lines) {
                        Log.d("line",line.text)
                        for (element in line.elements) {
                            Log.d("ele",element.text)
                        }
                    }
                }
                val nutriResult = nutriPattern.findAll(cameraresult.text)
                val calResult = calPattern.find(cameraresult.text)
                if (calResult != null) {
                    itemHash["열량"] = calResult.value
                }
                if(nutriResult!=null) {
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
    /*
        val result = detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                for (block in firebaseVisionText.textBlocks) {

                    val boundingBox = block.boundingBox
                    val cornerPoints = block.cornerPoints
                    val text = block.text
                    Toast.makeText(this,"성공", Toast.LENGTH_SHORT).show()
                    var cameraresult = findViewById<TextView>(R.id.cameraresult)
                    cameraresult.text = cameraresult.text.toString() + text.toString()
                    for (line in block.lines) {

                        for (element in line.elements) {

                        }

                    }

                }

            }

            .addOnFailureListener {

            }

        // [END run_detector_cloud]
*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data == null)
            finish()
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(imageFilePath)
            var exif: ExifInterface? = null

            try {
                exif = ExifInterface(imageFilePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val exifOrientation: Int
            val exifDegree: Int

            if (exif != null) {
                exifOrientation =
                    exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                exifDegree = exifOrientationToDegrees(exifOrientation)
            } else {
                exifDegree = 0
            }

            var photo = findViewById<ImageView>(R.id.cameraimage)
            photo.setImageBitmap(rotate(bitmap, exifDegree.toFloat()))
            imageFromBitmap(rotate(bitmap, exifDegree.toFloat()))
        }
    }

    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }

    private fun rotate(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun sendTakePhotoIntent():Boolean{
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            if(photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, packageName, photoFile!!)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                return true
            }
        }
        return false
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "TEST_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir          /* directory */
        )
        imageFilePath = image.getAbsolutePath()
        return image
    }

    companion object {

        private val REQUEST_IMAGE_CAPTURE = 672
    }
}
