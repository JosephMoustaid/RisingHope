function loadVideo() {
    const videoContainer = document.getElementById("videoContainer");
    videoContainer.innerHTML = `
        <iframe width="100%" height="100%"
        style="border-radius: 40px;"
            src="https://www.youtube.com/embed/Gi16lOPerL0?autoplay=1"
            title="YouTube video player" frameborder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
            allowfullscreen>
        </iframe>
    `;
}

function setDonationAmount(amount) {
    document.getElementById('donationInput').value = "$" + amount.toFixed(2);
    document.querySelectorAll(".amounts button").forEach(btn => btn.classList.remove("active"));
    event.target.classList.add("active");
}

function attachMetaData(event) {
    event.preventDefault();

    const charityProgramId = document.getElementById("charityProgramIdInput").value;
    const donationAmount = document.getElementById("donationAmountInput").value;

    // Construct the payment link with query parameters
    const paymentLink = `https://donate.stripe.com/test_5kAaFQ43F0j9gN2000?charity_program_id=${charityProgramId}&amount=${donationAmount}`;

    // Redirect to the payment link
    window.location.href = paymentLink;
}