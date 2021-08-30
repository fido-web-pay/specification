## Sample Merchant FWP Integration
This sample shows the code and steps required after the
client having clicked an FWP compliant icon like the following:

![FWP PAY](https://fido-web-pay.github.io/specification/images/fwp-pay.svg)


### FWP invocation
```javascript
const methodData = [{
  supportedMethods: "fido-web-pay/v1",
  data: {
    payeeName: "Space Shop",  // Shown in the UI
    networks: [{
      name: "https://emvco.com/fwp/mastercard"
    },{
      name: "https://emvco.com/fwp/visa"
    },{
      name: "https://bankdirect.com",
      networkData: "additional stuff..."
    }]
  }
}];

const request = new PaymentRequest(methodData, {
  id: "7040566321",   // Mandatory FWP item
  total: {label: null, amount: {currency: "EUR", value: "435.00"}}
});

// Custom payment invocation method
async function doPaymentRequest() {
  const response = await request.show();
  await response.complete("success");
  
  // Get the authorization data
  return response.details;
}
```
### Payment authorization UI:
![FWP WALLET](https://fido-web-pay.github.io/specification/images/ui.svg)

The UI is a part of the browser resident FWP wallet.

### Resulting authorization data
```json
{
  "paymentMethod": "https://bankdirect.com",
  "issuerId": "https://mybank.fr/payment",
  "encryptedAuthorization": "pQED•••xg3w"
}
```
<table><tr><td>
This JSON structure is supposed to be returned to the merchant 
through <code>fetch()</code> or HTTP POST.  Since no
information that is unique for the individual
like card numbers, is exposed externally, there is no
need for PCI certification.
FWP is due to this arrangement also GDPR compliant.
</td></tr></table>

The FWP assertion now needs to be augmented with additional merchant data 
and subsequently be sent to a PSP (Payment System Provider) for fulfillment.

Note that that each `paymentMethod` may require specific additional information,
as well as potentially relying on different PSPs.

Please turn to PSP documentation for details. 
