import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

plt.rcParams['font.sans-serif'] = ['SimSun', 'STSong', 'Arial Unicode MS', 'Heiti SC']
plt.rcParams['axes.unicode_minus'] = False

fig, ax = plt.subplots(1, 1, figsize=(10, 6.5))
ax.set_xlim(0, 10)
ax.set_ylim(0, 6.5)
ax.axis('off')

def draw_box(ax, x, y, w, h, text, bold=False, small=False, lw=1.2):
    rect = mpatches.FancyBboxPatch(
        (x - w/2, y - h/2), w, h,
        boxstyle="square,pad=0.15",
        facecolor='white', edgecolor='black', linewidth=lw
    )
    ax.add_patch(rect)
    fs = 7 if small else 8.5
    wt = 'bold' if bold else 'normal'
    ax.text(x, y, text, ha='center', va='center', fontsize=fs, fontweight=wt, color='black')

def h_line(ax, x1, x2, y):
    ax.plot([x1, x2], [y, y], 'k-', lw=0.8)

def v_line(ax, x, y1, y2):
    ax.plot([x, x], [y1, y2], 'k-', lw=0.8)

# ===== 顶部标题框 =====
draw_box(ax, 5.0, 6.1, 8.0, 0.55, '在线教育平台 功能架构', bold=True, lw=1.4)

# 竖线连接到四个模块
v_line(ax, 5.0, 5.82, 5.45)

# 横线
h_line(ax, 1.3, 8.7, 5.45)

# 四个竖线到四个模块
module_x = [1.4, 3.8, 6.2, 8.6]
for mx in module_x:
    v_line(ax, mx, 5.45, 5.15)

# ===== 四大模块 =====
modules = [
    ('用户与认证模块', 1.4),
    ('课程与内容管理模块', 3.8),
    ('直播教学模块', 6.2),
    ('作业与考试模块', 8.6),
]

module_children = {
    1.4: ['用户注册\n与登录', 'JWT\n令牌鉴权', '角色权限\n控制', '用户信息\n管理'],
    3.8: ['课程分类\n管理', '课程发布\n与编辑', '章节课时\n管理', '选课与进度\n追踪'],
    6.2: ['直播间\n创建管理', '推拉流\n地址配置', 'WebSocket\n实时弹幕', '录制\n回放管理'],
    8.6: ['题库管理\n五种题型', '手动/自动\n组卷', '在线答题\n自动评分', '作业布置\n提交批改'],
}

for label, mx in modules:
    draw_box(ax, mx, 4.85, 2.2, 0.45, label, bold=True)
    # 竖线连到子模块
    v_line(ax, mx, 4.62, 4.3)

# 横线连接子模块
child_y = 4.3
h_line(ax, 1.3, 8.7, child_y)

# 每个模块的子模块
for mx, children in module_children.items():
    n = len(children)
    spacing = 2.2 / (n + 1)
    for i, child in enumerate(children):
        cx = mx - 1.1 + spacing * (i + 1)
        # 竖线
        v_line(ax, cx, child_y, 4.0)
        draw_box(ax, cx, 3.7, 0.9, 0.5, child, small=True)

# ===== 分工标注 =====
ax.text(2.6, 2.6, 'Person A 负责', ha='center', va='center',
        fontsize=8, fontweight='bold', color='black',
        bbox=dict(boxstyle='square,pad=0.3', facecolor='white',
                   edgecolor='black', linewidth=0.8, linestyle='--'))
ax.text(7.4, 2.6, 'Person B 负责', ha='center', va='center',
        fontsize=8, fontweight='bold', color='black',
        bbox=dict(boxstyle='square,pad=0.3', facecolor='white',
                   edgecolor='black', linewidth=0.8, linestyle='--'))

# 虚线框标注 Person A 的两模块
ax.plot([0.3, 5.0, 5.0, 0.3, 0.3], [5.6, 5.6, 3.15, 3.15, 5.6],
        'k--', lw=0.8)
# 虚线框标注 Person B 的两模块
ax.plot([5.0, 9.7, 9.7, 5.0, 5.0], [5.6, 5.6, 3.15, 3.15, 5.6],
        'k--', lw=0.8)

# ===== 图注 =====
ax.text(5.0, 0.15, '图2-2  系统功能架构设计图', ha='center', va='center',
        fontsize=9, fontweight='bold', color='black')

plt.tight_layout(pad=0.5)
plt.savefig('/Users/qt/online-edu-platform/docs/diagrams/功能架构设计图.png',
            dpi=200, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close()
print('Done: 功能架构设计图')
