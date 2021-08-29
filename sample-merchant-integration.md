### Merchant invocation code
```javascript
// FWP sample merchant invocation code

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
  id: "7040566321",   // Mandatory for FWP
  total: {label: null, amount: {currency: "EUR", value: "435.00"}}
});

async function doPaymentRequest() {
  const response = await request.show();
  await response.complete("success");
  return response.details;
}
```
### Payment authorization UI:
![FWP Logo](https://fido-web-pay.github.io/specification/images/ui.svg)

The UI is a part of the browser resident wallet.
All authorization operations are performed entirely locally.

### Resulting authorization data
```json
{
  "paymentMethod": "https://bankdirect.com",
  "issuerId": "https://mybank.fr/payment",
  "encryptedAuthorization": "pQED•••xg3w"
}
```
<table><tr><td>
This JSON structure is supposed to be returned to the merchant through <code>fetch()</code> or HTTP POST.  Since no
information that is unique for the individual is exposed (including card numbers), there is no
need for PCI certification.  The scheme should therefore also be GDPR compliant.
</td></tr></table>

The FWP assertion now needs to be augmented with additional merchant data and subsequently be sent to a PSP (Payment System Provider) for fulfillment.  This part is PSP specific including the method used for authenticating the merchant.  It is also likely that different `paymentMethods` require different PSPs.
