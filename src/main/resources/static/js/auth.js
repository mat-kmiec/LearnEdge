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