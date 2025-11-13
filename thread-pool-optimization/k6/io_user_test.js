import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '10s', target: 10 },   // ramp-up
        { duration: '10s', target: 50 },   // 평탄 구간
        { duration: '10s', target: 100 },  // 더 올림
        { duration: '10s', target: 0 },    // 정리
    ],
};

export default function () {
    const res = http.get('http://localhost:8080/api/v1/users');
    check(res, {
        'status is 200': (r) => {
            const ok = r.status === 200;
            if (!ok) {
                console.log(`❌ 실패 응답: ${r.status} ${r.status_text}, headers: ${JSON.stringify(r.headers)}`);
            }
            return ok;
        },
    });
    sleep(0.1);
}