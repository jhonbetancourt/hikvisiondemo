    var imgtag = document.getElementById('imageCrop');
    var placeholderImageSrc = "./img/person_image_placeholder.png";
    var registerUrl = "./register";
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

    $("#imgModal .close-modal").on("click", function(){
        $("#imageCrop").attr("src",placeholderImageSrc);
        $("#imgFileInput").val("");
         if(cropper!=null){
            cropper.destroy();
        }

    });

    $("#imgModal .ok-modal").on("click", function(){
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

    $('#formPerson').submit(function(e){
        e.preventDefault();

        if($("#inputBase64Image").val().length==0){
            showMessageModal("Mensaje", "Es requerido seleccionar la imagen de la persona");
            return false;
        }

        var clickedSubmit = $(this).find('[type=submit]');
        $(clickedSubmit).prop('disabled', true);
        $("#spinnerModal").modal('show');

        var data = getFormData($(this));

        $.ajax({
           type: "POST",
           headers: {
                   'Accept': 'application/json',
                   'Content-Type': 'application/json'
               },
           url: registerUrl,
           dataType: "json",
           data: JSON.stringify(data),
           success: function(result){
                resetFormPerson();
                $(clickedSubmit).prop('disabled', false);
                $("#spinnerModal").modal('hide');
                showMessageModal("Mensaje", result.message);
           },
           error: function(xhr, status, error) {

                $("#spinnerModal").modal('hide');

                try{
                    showMessageModal("Error", xhr.responseJSON.message);
                }catch(e){
                    showMessageModal("Error", "Error al registrar la persona");
                }

                $(clickedSubmit).prop('disabled', false);
           }
         });

        return true;
    });

    function resetFormPerson(){
        $('#formPerson').trigger("reset");
        $("#personImg").attr("src", placeholderImageSrc);
    }

    function showMessageModal(title, message){
        if(title==null || title.length==0){
            $("#messageModal .modal-title").css("display","none");
        }else{
            $("#messageModal .modal-title").css("display","");
        }
        $("#messageModal .modal-title").html(title);
        $("#messageModal .pMessage").html(message);
        $("#messageModal").modal('show');
    }

    function hideMessageModal(){
        $("#messageModal .modal-title").html("");
        $("#messageModal .pMessage").html("");
        $("#messageModal").modal('hide');
    }

    function getFormData($form){
        var unindexed_array = $form.serializeArray();
        var indexed_array = {};

        $.map(unindexed_array, function(n, i){
            indexed_array[n['name']] = n['value'];
        });

        return indexed_array;
    }


