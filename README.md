

# Vault Cloud - Signature Provider PDF Handler #

PDF Handler - Preparere pdf fields/attachments before signature

## Built-in API - SYNC MODE ##
| PATH | /prepare-file-fields-attachments |
|--|--|
| Method | POST |
| Headers | Content-Type: application/json |

``` 
Request
{
	"pdfFile": "/tmp/pdfexemplo.pdf",
	"append": true,
	"autoFixDocument": true,
	"fields": [{
		"type": "text or image",
		"value": "text or filepath",
		"option_required": false,
		"option_multiline": false,
		"option_read_only": false,
		"option_do_not_scroll": false,
		"option_password": false,
		"border_color": "WHITE, LIGHT_GRAY, GRAY, DARK_GRAY, BLACK, RED, PINK, ORANGE, YELLOW, GREEN, MAGENTA, CYAN OR BLUE",
		"border_style": "SOLID, DASHED, BEVELED, INSET, UNDERLINE",
		"border_width": 1,
		"x": "Determines horizontal coordinate",
		"y": "Determines vertical coordinate",
		"height": "Determines the field height in pixels",
		"width": "Determines the field width in pixels",
		"page": 1
	}],
	"attachments": [{
		"file": "/tmp/certificate-attribute.pem",
		"description": "My favorite attribute certificate xpto123",
	}]
}

Response:
{
    "file": "/tmp/pdfexemplo_prepared.pdf",
    "message": "File prepareted!!!!",
    "status": "OK"
}
``` 
      
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



