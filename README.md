

# Vault Cloud - Signature Provider PDF Handler #

      
PDF Handler - Preparer to signature

## Built-in API - SYNC MODE ##
| PATH | /prepare-file-to-signature |
|--|--|
| Method | POST |
| Headers | Content-Type: application/json |

``` 
Request
{
	"pdfFile": "/home/paulo/Área de Trabalho/pdfexemplo.pdf",
	"append": true,
	"autoFixDocument": true,
	"isVisibleSignature": true,
	"imageFile": "/home/paulo/Área de Trabalho/signature.png",
	"page": "0",
	"x": 355,
	"y": 782,
	"width": 230,
	"heigth": 50,
	"reason": "My reason",
	"location": "My location",
	"contact": "My contact",
	"signerName": "PAULO FILIPE MACEDO DOS SANTOS:04660457192",
	"subfilter": "adbe.pkcs7.detached"
}

Response:
{
	"pdfFile": "/home/paulo/Área de Trabalho/pdfexemplo.pdf",
	"detail": {
        "code": 1000,
        "status": "FILE_PREPARED",
        "message": ""
    }
}
``` 



