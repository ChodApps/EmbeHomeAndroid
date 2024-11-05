package com.embehome.embehome.Fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.embehome.embehome.Adapter.ImageGridAdapter
import com.embehome.embehome.R
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.databinding.FragmentCreateAreaBinding
import com.embehome.embehome.rules.utils.RuleDataRepository
import com.embehome.embehome.viewModel.CreateAreaViewModel

/**
 * A simple [Fragment] subclass.
 */
class CreateArea : Fragment() {

    val viewModel: CreateAreaViewModel by viewModels()
    val args : CreateAreaArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.id > 0) {
            if (args.type == "Rule") {
                RuleDataRepository.getRuleItem(args.id).also {
                    if (it.rulename_id == args.id) {
                        viewModel.areaName.value = it.rule_name
                        viewModel.areaImage.value = it.image
                    }
                }
            }
            else if (args.type == "Scene") {
                CacheSceneTwoWay.getSceneItem(args.id).also {
                    if (it.scenename_id == args.id) {
                        viewModel.areaName.value = it.scene_name
                        viewModel.areaImage.value = it.image
                    }
                }
            }
        }
    }


    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentCreateAreaBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_area, container, false)

        if (args.action != "nil" && args.type != "nil") {
            binding.textView226.text = "${args.action} ${args.type}"
        }
        else {
            findNavController().navigateUp()
        }

        binding.listImageGrid.adapter = ImageGridAdapter(requireContext())

        viewModel.createAreaBack.observe(viewLifecycleOwner, Observer {
            if (it)
                findNavController().navigateUp()
        })

        viewModel.areaImage.observe(viewLifecycleOwner, Observer {
            if (it != null){
                binding.imageView265.setImageBitmap(it)
            }
        })

        viewModel.saveArea.observe(viewLifecycleOwner, Observer {
            if (it) {
               /* if (viewModel.areaImage.value != null) {*/
            /*        val f = File(currentPhotoPath)
                    val m = RequestBody.create(MediaType.parse("image/png"), f)
                    val b = MultipartBody.Part.createFormData("area_image","android_upload.png", m)*/
                if (viewModel.validateImage(requireContext())) {

                    when (args.type) {

                        "Scene" -> {
                            if (args.id <= 0) {
                                viewModel.createSceneItem(requireContext())
                            } else {
                                viewModel.updateSceneItem(requireContext(), args.id)
                            }
                        }

                        "Rule" -> {
                            if (args.action == "Create") {
                                viewModel.createRuleItem(requireContext())
                            } else {
                                if (args.id > 0) {
                                    viewModel.updateRule(requireContext(), args.id)
                                }
                            }

                            /*val c = MultipartBody.Part.createFormData("rule_image","android_upload.JPEG", m)
                            viewModel.createRuleItem(requireContext(), viewModel.areaName.value ?: "Rule 1", c, f)*/
                        }


                    }
//                    viewModel.saveArea(requireContext(), viewModel.areaName.value ?: "area", b, f)
                    /*viewModel.areaImage.value?.let {
                        compressImage(it)
                    }*/
                }

                }
        })

        viewModel.importImage.observe(viewLifecycleOwner, Observer {
            if (it) {

                viewModel.readImage(requireContext(), requireActivity(), this)
/*

                Log.d("TronX", "Open Camera or Gallery")
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                     ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE ),
                        3
                    )

                } else {
                    AppDialogs.openTitleDialog(requireContext(),"Choose Image","From where do you want to select the image from","", {dialog, which ->

                    }, "Camera", {dialog, which ->
                        openCamera()
                    }, "Gallery", {dialog, which ->
                        val pickPhoto = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(pickPhoto, 2)
                    })

                }
*/
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this){
            findNavController().navigateUp()
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

/*
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", *//* prefix *//*
            ".JPEG", *//* suffix *//*
            storageDir *//* directory *//*
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            if (::currentPhotoPath.isInitialized && currentPhotoPath.isNotEmpty()) File(currentPhotoPath).delete()
            currentPhotoPath = absolutePath
        }
    }*/

    /*private fun createTempImageFile () : File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", *//* prefix *//*
            ".JPEG", *//* suffix *//*
            storageDir *//* directory *//*
        )
    }*/

    /*private fun openCamera () {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicture ->
            takePicture.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.tronx.android.fileprovider",
                        it
                    )
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePicture, 1)
                }
            }
        }

    }*/

    /*fun compressImage (img : Bitmap) {
        val imageConstant = 8000000f
        val ei = ExifInterface(currentPhotoPath)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )


        Log.d("Tronx","${ ExifInterface.ORIENTATION_ROTATE_90} ${ ExifInterface.ORIENTATION_ROTATE_180}" +
                " ${ ExifInterface.ORIENTATION_ROTATE_270}  ${ ExifInterface.ORIENTATION_NORMAL}   $orientation")

        Log.d("TronX", "original w = ${img.width} and h = ${img.height}   density = ${img.density}")
        val size = img.height * img.width
        if (size > imageConstant) {
            val r = (imageConstant / size) * 100
            val file = createTempImageFile()
            val op = FileOutputStream(file)
            img.compress(Bitmap.CompressFormat.JPEG, 50, op)
            val compresedPic = BitmapFactory.decodeFile(file.absolutePath)
            Log.d("TronX", "compressed w = ${compresedPic.width} and h = ${compresedPic.height} ${file.totalSpace}  ${file.freeSpace}  ${compresedPic.density}")
        }
        else {

        }

    }*/


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TronX", "Image data received $requestCode $requestCode")
        if (requestCode == 1 && resultCode == -1) {
           /* viewModel.areaImage.value = data?.extras?.get("data") as Bitmap?
            if (viewModel.areaImage.value == null) {
                val f = File(currentPhotoPath)
                viewModel.areaImage.value = BitmapFactory.decodeFile(currentPhotoPath)
            }*/
            viewModel.processCameraImage()
        } else if (requestCode == 2 && resultCode == -1) {

            viewModel.processGalleryImage(data, requireContext(), requireActivity())
            /*
            if (data != null) {
                createImageFile()
                if (Build.VERSION.SDK_INT >= 29) {
                    try {
                        data.data?.let {
                            requireActivity().contentResolver.openFileDescriptor(it, "r").use { pfd ->
                                if (pfd != null) {
                                    val bitmap = BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
                                    if (bitmap != null) {
                                        viewModel.areaImage.value = bitmap
                                        val out = FileOutputStream(currentPhotoPath)
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                                        out.close()
                                    }
                                }
                            }
                        }
                    } catch (ex: IOException) {
                        AppServices.log("TronX","Gallery read image A10 - ${ex.message}")
                    }
                }

                else {
                    val selectedImage = data.data
                    val filePath: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
                    val c =
                        selectedImage?.let {
                            requireActivity().applicationContext.contentResolver.query(
                                it, filePath,
                                null, null, null
                            )
                        }
                    c?.apply {
                        moveToFirst()
                        getColumnIndex(filePath[0]).let {
                            c.getString(it)?.also {
                                val bitmap = BitmapFactory.decodeFile(it)
                                if (bitmap != null) {
                                    viewModel.areaImage.value = bitmap
                                    val out = FileOutputStream(currentPhotoPath)
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                                    out.close()
                                }
                            }
                        }
                    }

                }
            }*/

        }
    }


   /* override fun onDestroy() {
        super.onDestroy()
       *//* try {
            if (::currentPhotoPath.isInitialized && currentPhotoPath.isNotEmpty()) File(currentPhotoPath).delete()
        }
        catch (e : Exception) {

        }*//*
    }*/


}
