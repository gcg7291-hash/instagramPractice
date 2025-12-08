document.addEventListener('DOMContentLoaded', () => {
    const switchElement = document.getElementById('darkModeSwitch');
    const themeIcon = document.getElementById('theme-icon');
    const body = document.body;

    // 1. 테마 적용 및 상태 저장 함수
    const applyTheme = (isDark) => {
        if (isDark) {
            body.classList.add('dark-mode');
            // 아이콘 변경
            if(themeIcon) themeIcon.classList.replace('bi-sun-fill', 'bi-moon-fill');
            if(switchElement) switchElement.checked = true;
            localStorage.setItem('theme', 'dark');
        } else {
            body.classList.remove('dark-mode');
            // 아이콘 변경
            if(themeIcon) themeIcon.classList.replace('bi-moon-fill', 'bi-sun-fill');
            if(switchElement) switchElement.checked = false;
            localStorage.setItem('theme', 'light');
        }
    };

    // 2. 초기 로드 시 저장된 테마 불러오기
    const savedTheme = localStorage.getItem('theme');

    if (savedTheme === 'dark') {
        applyTheme(true);
    } else if (savedTheme === 'light') {
        applyTheme(false);
    } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        // 저장된 설정이 없으면 OS 설정 따르기
        applyTheme(true);
    } else {
        applyTheme(false);
    }


    // 3. 스위치 변경 이벤트 핸들러
    if (switchElement) {
        switchElement.addEventListener('change', (event) => {
            applyTheme(event.target.checked);
        });
    }
});