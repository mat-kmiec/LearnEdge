// toggle password visibility
document.addEventListener('click', (e) => {
    const btn = e.target.closest('[data-toggle-target]');
    if (!btn) return;
    const input = document.querySelector(btn.dataset.toggleTarget);
    if (!input) return;
    const isPwd = input.type === 'password';
    input.type = isPwd ? 'text' : 'password';
    btn.setAttribute('aria-pressed', String(isPwd));
});

// handle profile picture upload
async function handleProfilePictureChange(input) {
    if (input.files && input.files[0]) {
        const file = input.files[0];
        console.log('Selected file:', file.name);
        console.log('File size:', file.size);
        console.log('File type:', file.type);
        
        const formData = new FormData();
        formData.append('profilePicture', file);

        try {
            const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            
            const response = await fetch('/api/profile/picture', {
                method: 'POST',
                body: formData,
                headers: {
                    [header]: token
                }
            });

            console.log('Response status:', response.status);
            const data = await response.json();
            console.log('Response data:', data);
            
            if (!response.ok) {
                throw new Error(data.error || 'Błąd podczas przesyłania zdjęcia');
            }
            
            // Update profile picture on the page
            const profileImage = document.getElementById('profileImage');
            if (profileImage && data.url) {
                console.log('Updating profile picture to:', data.url);
                profileImage.src = data.url;
            } else {
                console.log('Could not update profile picture. Image element:', profileImage, 'URL:', data.url);
            }
        } catch (error) {
            console.error('Detailed error:', error);
            alert(error.message || 'Wystąpił błąd podczas przesyłania zdjęcia. Spróbuj ponownie.');
            console.error('Error uploading profile picture:', error);
        }
    }
}

// register form validation + strength meter
function validateRegister(ev){
    const form = ev.target;
    const u = form.querySelector('#username');
    const p = form.querySelector('#password');
    if (!u.value || u.value.length < 3){ alert('Nazwa użytkownika min. 3 znaki'); u.focus(); ev.preventDefault(); return false; }
    if (!p.value || p.value.length < 6){ alert('Hasło min. 6 znaków'); p.focus(); ev.preventDefault(); return false; }
    return true;
}

// live password strength
(function(){
    const pwd = document.querySelector('#password');
    const meter = document.querySelector('#strength');
    const label = document.querySelector('#strengthLabel');
    if(!pwd || !meter || !label) return;

    const score = (v) => {
        let s = 0;
        if (v.length >= 6) s++;
        if (/[A-Z]/.test(v)) s++;
        if (/[0-9]/.test(v)) s++;
        if (/[^A-Za-z0-9]/.test(v)) s++;
        return s;
    };
    const names = ['bardzo słabe','słabe','średnie','dobre','bardzo dobre'];

    pwd.addEventListener('input', () => {
        const s = score(pwd.value);
        meter.value = s;
        label.textContent = `Siła hasła: ${names[s] ?? names[0]}`;
    });
})();