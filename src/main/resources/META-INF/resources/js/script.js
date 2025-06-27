function copySecret() {
    const secretInput = document.getElementById('secretInput');
    const secretText = secretInput.value;
    const copyBtn = document.getElementById('copyBtn');

    // Select the input text for visual feedback
    secretInput.select();
    secretInput.setSelectionRange(0, 99999); // For mobile devices

    // Use the modern Clipboard API
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(secretText).then(() => {
            showCopySuccess(copyBtn);
        }).catch(err => {
            console.error('Failed to copy: ', err);
            fallbackCopyTextToClipboard(secretText, copyBtn);
        });
    } else {
        // Fallback for older browsers or non-HTTPS
        fallbackCopyTextToClipboard(secretText, copyBtn);
    }
}

function fallbackCopyTextToClipboard(text, button) {
    const secretInput = document.getElementById('secretInput');

    try {
        secretInput.select();
        secretInput.setSelectionRange(0, 99999);
        const successful = document.execCommand('copy');
        if (successful) {
            showCopySuccess(button);
        } else {
            console.error('Fallback: Could not copy text');
        }
    } catch (err) {
        console.error('Fallback: Unable to copy', err);
    }
}

function showCopySuccess(button) {
    // Change button appearance
    button.classList.add('copied');
    button.innerHTML = `
        <svg class="copy-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
        </svg>
        Copied!
    `;

    // Reset after 2 seconds
    setTimeout(() => {
        button.classList.remove('copied');
        button.innerHTML = `
            <svg class="copy-icon" viewBox="0 0 24 24" fill="currentColor">
                <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
            </svg>
            Copy
        `;
    }, 2000);
}