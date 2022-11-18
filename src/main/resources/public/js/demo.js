    var imgtag = document.getElementById('imageCrop');
    var placeholderImageSrc = "/img/person_image_placeholder.png";
    var cropBoxData;
    var canvasData;
    var cropper;

    function onFileSelected(event) {
      var selectedFile = event.target.files[0];
      var reader = new FileReader();

      imgtag.title = selectedFile.name;

      reader.onload = function(event) {
        imgtag.src = event.target.result;

        cropper = new Cropper(imgtag, {
          aspectRatio: 4 / 5,
          autoCropArea: 0.5,
          ready: function () {
            cropper.setCropBoxData(cropBoxData).setCanvasData(canvasData);
          }
        });
      };

      reader.readAsDataURL(selectedFile);
    }

    $(".close-modal").on("click", function(){
        $("#imageCrop").attr("src",placeholderImageSrc);
        $("#imgFileInput").val("");
         if(cropper!=null){
            cropper.destroy();
        }

    });

    $(".ok-modal").on("click", function(){
        cropBoxData = cropper.getCropBoxData();
        canvasData = cropper.getCanvasData();
        if($("#imgFileInput").val().length>0){
            var imgData = cropper.getCroppedCanvas({fillColor:'#ffffff'}).toDataURL('image/jpeg');
            $("#personImg").attr("src", imgData);
            $("#inputBase64Image").val(imgData);
        }
        $("#imgFileInput").val("");
        $("#imageCrop").attr("src", placeholderImageSrc);
        cropper.destroy();
    });

    $('#formPerson').submit(function(){
        if($("#inputBase64Image").val().length==0){
            alert("Es requerido seleccionar una fotografia");
            return false;
        }
        var clickedSubmit = $(this).find('[type=submit]');
        $(clickedSubmit).prop('disabled', true);
        return true;
    });
