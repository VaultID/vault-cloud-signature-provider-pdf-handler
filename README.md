

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
	"pdfFile": "/tmp/pdfexemplo.pdf",
	"append": true,
	"autoFixDocument": true,
	"isVisibleSignature": true,
	"imageFile": "/tmp/signature.png",
	"page": 1,
	"x": 355,
	"y": 782,
	"width": 230,
	"height": 50,
	"reason": "My reason",
	"location": "My location",
	"contact": "My contact",
	"signerName": "PAULO FILIPE MACEDO DOS SANTOS:04660457192",
	"subfilter": "adbe.pkcs7.detached"
}

Response:
{
    "file": "/tmp/pdfexemplo_prepared.pdf",
    "message": "File prepareted!!!!",
    "status": "OK"
}
``` 



